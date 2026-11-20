package com.procura.config.dto;

import com.procura.config.domain.ConfigSetting;

public record ConfigDto(
    String key,
    String value,
    String description
) {
    public static ConfigDto from(ConfigSetting setting) {
        return new ConfigDto(setting.getSettingKey(), setting.getSettingValue(), setting.getDescription());
    }
}
