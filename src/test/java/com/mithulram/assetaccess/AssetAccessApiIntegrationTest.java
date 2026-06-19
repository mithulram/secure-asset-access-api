package com.mithulram.assetaccess;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class AssetAccessApiIntegrationTest {

    private static final String ADMIN_PASSWORD = "demo-admin-change-me";
    private static final String VIEWER_PASSWORD = "demo-viewer-change-me";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void healthIsPublicButAssetsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void nonHealthActuatorEndpointsAreNotPublic() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidBasicCredentialsAreRejectedWithoutDetails() throws Exception {
        mockMvc.perform(get("/api/assets").with(httpBasic("admin", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void administratorCanCreateAssetAndAuditEventWithHttpBasic() throws Exception {
        UUID assetId = createAssetAsAdminWithBasicAuth();

        mockMvc.perform(get("/api/assets/{assetId}", assetId).with(httpBasic("viewer", VIEWER_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ECU diagnostics gateway"))
                .andExpect(jsonPath("$.classification").value("RESTRICTED"));

        mockMvc.perform(get("/api/audit-events").with(httpBasic("admin", ADMIN_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("ASSET_CREATED"))
                .andExpect(jsonPath("$[0].actor").value("admin"));
    }

    @Test
    void administratorCanCreateAssetAndAuditEvent() throws Exception {
        UUID assetId = createAssetAsAdmin();

        mockMvc.perform(get("/api/assets/{assetId}", assetId).with(user("viewer").roles("VIEWER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ECU diagnostics gateway"))
                .andExpect(jsonPath("$.classification").value("RESTRICTED"));

        mockMvc.perform(get("/api/audit-events").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("ASSET_CREATED"))
                .andExpect(jsonPath("$[0].actor").value("admin"));
    }

    @Test
    void onlyAdministratorsCanCreateAssetsAndGrants() throws Exception {
        mockMvc.perform(post("/api/assets")
                        .with(user("operator").roles("OPERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetPayload()))
                .andExpect(status().isForbidden());

        UUID assetId = createAssetAsAdmin();
        mockMvc.perform(post("/api/assets/{assetId}/access-grants", assetId)
                        .with(user("operator").roles("OPERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"principal\":\"contractor-a\",\"permission\":\"VIEW\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerCannotCreateAssetsWithHttpBasic() throws Exception {
        mockMvc.perform(post("/api/assets")
                        .with(httpBasic("viewer", VIEWER_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetPayload()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void administratorCanGrantAccessAndViewerCanReadGrantRegistry() throws Exception {
        UUID assetId = createAssetAsAdmin();

        mockMvc.perform(post("/api/assets/{assetId}/access-grants", assetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"principal\":\"vehicle-validation-team\",\"permission\":\"OPERATE\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assetId").value(assetId.toString()))
                .andExpect(jsonPath("$.permission").value("OPERATE"));

        mockMvc.perform(get("/api/assets/{assetId}/access-grants", assetId)
                        .with(user("viewer").roles("VIEWER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].principal").value("vehicle-validation-team"));
    }

    @Test
    void validationAndUnknownAssetsReturnUsefulHttpErrors() throws Exception {
        mockMvc.perform(post("/api/assets")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"ownerTeam\":\"\",\"classification\":null}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/assets/{assetId}", UUID.randomUUID())
                        .with(user("viewer").roles("VIEWER")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Asset not found"));
    }

    private UUID createAssetAsAdmin() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/assets")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetPayload()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(response.get("id").asText());
    }

    private UUID createAssetAsAdminWithBasicAuth() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/assets")
                        .with(httpBasic("admin", ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetPayload()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(response.get("id").asText());
    }

    private String assetPayload() {
        return "{\"name\":\"ECU diagnostics gateway\",\"ownerTeam\":\"Vehicle Security\",\"classification\":\"RESTRICTED\"}";
    }
}
