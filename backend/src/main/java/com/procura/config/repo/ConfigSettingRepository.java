package com.procura.config.repo;

import com.procura.config.domain.ConfigSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigSettingRepository extends JpaRepository<ConfigSetting, Long> {
    Optional<ConfigSetting> findBySettingKey(String settingKey);
}
