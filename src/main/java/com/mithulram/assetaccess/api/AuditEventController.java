package com.mithulram.assetaccess.api;

import com.mithulram.assetaccess.service.AssetAccessService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-events")
public class AuditEventController {
    private final AssetAccessService service;

    public AuditEventController(AssetAccessService service) {
        this.service = service;
    }

    @GetMapping
    public List<AuditEventResponse> listAuditEvents() {
        return service.listAuditEvents().stream().map(AuditEventResponse::from).toList();
    }
}
