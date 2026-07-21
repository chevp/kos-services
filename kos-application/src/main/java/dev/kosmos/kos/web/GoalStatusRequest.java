package dev.kosmos.kos.web;

import dev.kosmos.kos.domain.GoalStatus;

public record GoalStatusRequest(
        GoalStatus status
) {}
