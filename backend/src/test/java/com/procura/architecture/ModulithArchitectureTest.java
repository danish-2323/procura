package com.procura.architecture;

import com.procura.ProcuraApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithArchitectureTest {

    @Test
    void verifyModularStructure() {
        ApplicationModules modules = ApplicationModules.of(ProcuraApplication.class);
        modules.verify();
    }
}
