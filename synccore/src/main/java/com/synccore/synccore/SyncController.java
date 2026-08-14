package com.synccore.synccore;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.synccore.synccore.dto.SyncRequest;
import com.synccore.synccore.dto.SyncResponse;

public abstract class SyncController<T extends Syncable> {

    protected abstract SyncService<T> syncService();

    @PostMapping("/sync")
    public SyncResponse<T> sync(@RequestBody SyncRequest<T> request,
                                @AuthenticationPrincipal Jwt jwt) {
        UUID owner = UUID.fromString(jwt.getSubject());
        return syncService().sync(request, owner);
    }
}

