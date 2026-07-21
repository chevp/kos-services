package dev.kosmos.kos.web;

import dev.kosmos.kos.domain.AgentStatus;

public record AgentStatusRequest(
        AgentStatus status
) {}
