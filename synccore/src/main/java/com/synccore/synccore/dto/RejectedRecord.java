package com.synccore.synccore.dto;

import java.util.List;
import java.util.UUID;

public record RejectedRecord(UUID id, List<String> errors) { }
