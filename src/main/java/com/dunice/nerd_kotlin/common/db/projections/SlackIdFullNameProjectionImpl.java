package com.dunice.nerd_kotlin.common.db.projections;

import lombok.Data;

@Data
public class SlackIdFullNameProjectionImpl implements SlackIdFullNameProjection {
    private final String FullName;
    private final String SlackId;
}
