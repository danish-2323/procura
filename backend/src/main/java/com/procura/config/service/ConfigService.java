package com.procura.config.service;

import com.procura.config.domain.ConfigSetting;
import com.procura.config.dto.ConfigDto;
import com.procura.config.dto.UpdateConfigSettingRequest;
import com.procura.config.repo.ConfigSettingRepository;
import com.procura.shared.audit.AuditService;
import com.procura.shared.audit.AuditableAction;
import com.procura.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigSettingRepository configSettingRepository;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<ConfigDto> getAllSettings() {
        return configSettingRepository.findAll().stream()
            .map(ConfigDto::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public String getSetting(String key, String defaultValue) {
        return configSettingRepository.findBySettingKey(key)
            .map(ConfigSetting::getSettingValue)
            .orElse(defaultValue);
    }

    @Transactional
    public ConfigDto updateSetting(UpdateConfigSettingRequest req, Long userId, String clientIp, String traceId) {
        ConfigSetting setting = configSettingRepository.findBySettingKey(req.key())
            .orElseThrow(() -> new ResourceNotFoundException("ConfigSetting", req.key()));

        String oldValue = setting.getSettingValue();
        setting.setSettingValue(req.value());
        ConfigSetting saved = configSettingRepository.save(setting);

        auditService.recordStandalone(
            userId, null, AuditableAction.CONFIG_CHANGED, "CONFIG", saved.getId(),
            oldValue, req.value(), clientIp, traceId
        );

        return ConfigDto.from(saved);
    }
}
