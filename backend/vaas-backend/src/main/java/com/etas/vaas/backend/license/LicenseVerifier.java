/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.license.LicenseVerifier
 *  com.etas.vaas.backend.license.LicenseVerifier$1
 *  de.schlichtherle.license.CipherParam
 *  de.schlichtherle.license.DefaultCipherParam
 *  de.schlichtherle.license.DefaultLicenseParam
 *  de.schlichtherle.license.KeyStoreParam
 *  de.schlichtherle.license.LicenseManager
 *  de.schlichtherle.license.LicenseParam
 */
package com.etas.vaas.backend.license;

import java.security.KeyStore;

import com.etas.vaas.backend.license.LicenseVerifier;
import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.DefaultCipherParam;
import de.schlichtherle.license.DefaultLicenseParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;
import java.io.File;
import java.util.prefs.Preferences;

/*
 * Exception performing whole class analysis ignored.
 */
public class LicenseVerifier {
    private static final String LICENSE_FILE_PATH = "C:\\Users\\SOQ2WX\\Etas\\etas_e2e_vaas\\vaas_cloud_backend\\src\\main\\resources\\license.lic";
    private static final String PUBLIC_KEY_STORE_PATH = "/publicCerts.store";
    private static final String STORE_PASSWORD = "store123";
    private static final String PUBLIC_ALIAS = "publickey";

    public static void main(String[] args) {
        try {
            LicenseManager licenseManager = new LicenseManager(LicenseVerifier.initLicenseParam());
            licenseManager.install(new File("C:\\Users\\SOQ2WX\\Etas\\etas_e2e_vaas\\vaas_cloud_backend\\src\\main\\resources\\license.lic"));
            licenseManager.verify();
            System.out.println("\u2705 License verification successful.");
        }
        catch (Exception e) {
            System.err.println("\u274c License verification failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static LicenseParam initLicenseParam() {
        Preferences preferences = Preferences.userNodeForPackage(LicenseVerifier.class);
        DefaultCipherParam cipherParam = new DefaultCipherParam("key123");
        if (LicenseVerifier.class.getResource("/publicCerts.store") == null) {
            throw new RuntimeException("\u274c \u516c\u94a5\u5e93\u6587\u4ef6\u672a\u627e\u5230: /publicCerts.store");
        }
        // TODO: 许可证验证 - 需要原始密钥库文件
            KeyStoreParam publicKeyStoreParam = null; // placeholder
            /* KeyStoreParam publicKeyStoreParam = new KeyStoreParam() { ... } */
        return new DefaultLicenseParam("MyCompany-License", preferences, (KeyStoreParam)publicKeyStoreParam, (CipherParam)cipherParam);
    }
}

