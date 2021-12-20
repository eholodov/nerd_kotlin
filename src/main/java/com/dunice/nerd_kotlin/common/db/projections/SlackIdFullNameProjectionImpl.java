package com.dunice.nerd_kotlin.common.db.projections;

import com.dunice.nerd_kotlin.common.db.projections.SlackIdFullNameProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class SlackIdFullNameProjectionImpl implements SlackIdFullNameProjection {
    private String SlackId;
    private String FullName;

    @NotNull
    @Override
    public String getSlackId() {
        return this.SlackId;
    }

    @NotNull
    @Override
    public String getFullName() {
        return this.FullName;
    }
}
