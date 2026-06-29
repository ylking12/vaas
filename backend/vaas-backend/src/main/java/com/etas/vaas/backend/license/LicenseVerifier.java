/*
 * Decompiled with CFR 0.152.
 *
 * SOURCE: Decompiled from vaas-backend.jar | ORIGINAL: com.etas.vaas.backend.license.LicenseVerifier | STATUS: Restored
 */
package com.etas.vaas.backend.license;

import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.DefaultCipherParam;
import de.schlichtherle.license.DefaultKeyStoreParam;
import de.schlichtherle.license.DefaultLicenseParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;
import java.io.File;
import java.util.prefs.Preferences;

public class LicenseVerifier {
    private static final String LICENSE_FILE_PATH = "C:\\Users\\SOQ2WX\\Etas\\etas_e2e_vaas\\vaas_cloud_backend\\src\\main\\resources\\license.lic";
    private static final String PUBLIC_KEY_STORE_PATH = "/publicCerts.store";
    private static final String STORE_PASSWORD = "store123";
    private static final String PUBLIC_ALIAS = "publickey";

    public static void main(String[] args) {
        try {
            LicenseManager licenseManager = new LicenseManager(LicenseVerifier.initLicenseParam());
            licenseManager.install(new File(LICENSE_FILE_PATH));
            licenseManager.verify();
            System.out.println("License verification successful.");
        }
        catch (Exception e) {
            System.err.println("License verification failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static LicenseParam initLicenseParam() {
        Preferences preferences = Preferences.userNodeForPackage(LicenseVerifier.class);
        DefaultCipherParam cipherParam = new DefaultCipherParam("key123");
        if (LicenseVerifier.class.getResource(PUBLIC_KEY_STORE_PATH) == null) {
            throw new RuntimeException("Public key store file not found: " + PUBLIC_KEY_STORE_PATH);
        }
        // DefaultKeyStoreParam 从 classpath 加载 .store 文件
        // 等价于反编译缺失的匿名内部类（原 LicenseVerifier$1）
        // 注：PKCS12 格式下 storePwd 与 keyPwd 必须相同
        KeyStoreParam publicKeyStoreParam = new DefaultKeyStoreParam(
            LicenseVerifier.class,
            PUBLIC_KEY_STORE_PATH,
            PUBLIC_ALIAS,
            STORE_PASSWORD,
            STORE_PASSWORD
        );
        return new DefaultLicenseParam("MyCompany-License", preferences, publicKeyStoreParam, cipherParam);
    }
}
