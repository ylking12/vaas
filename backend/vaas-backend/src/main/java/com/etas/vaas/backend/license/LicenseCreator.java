/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.license.LicenseCreator
 *  de.schlichtherle.license.CipherParam
 *  de.schlichtherle.license.DefaultCipherParam
 *  de.schlichtherle.license.DefaultKeyStoreParam
 *  de.schlichtherle.license.DefaultLicenseParam
 *  de.schlichtherle.license.KeyStoreParam
 *  de.schlichtherle.license.LicenseContent
 *  de.schlichtherle.license.LicenseManager
 *  de.schlichtherle.license.LicenseParam
 */
package com.etas.vaas.backend.license;

import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.DefaultCipherParam;
import de.schlichtherle.license.DefaultKeyStoreParam;
import de.schlichtherle.license.DefaultLicenseParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;
import java.io.File;
import java.util.Date;
import java.util.prefs.Preferences;
import javax.security.auth.x500.X500Principal;

public class LicenseCreator {
    private static final String PRIVATE_KEY_STORE_PATH = "/privateKeys.store";
    private static final String LICENSE_OUTPUT_PATH = "C:\\Users\\SOQ2WX\\Etas\\etas_e2e_vaas\\vaas_cloud_backend\\src\\main\\resources\\license.lic";
    private static final String STORE_PASSWORD = "store123";
    private static final String KEY_PASSWORD = "key123";
    private static final String ALIAS = "privatekey";

    public static void main(String[] args) throws Exception {
        LicenseCreator creator = new LicenseCreator();
        creator.generateLicense();
        System.out.println("Writing license to: " + new File(LICENSE_OUTPUT_PATH).getAbsolutePath());
    }

    public void generateLicense() throws Exception {
        LicenseManager licenseManager = new LicenseManager(this.initLicenseParam());
        LicenseContent content = this.createLicenseContent();
        licenseManager.store(content, new File(LICENSE_OUTPUT_PATH));
        System.out.println("License generated at: C:\\Users\\SOQ2WX\\Etas\\etas_e2e_vaas\\vaas_cloud_backend\\src\\main\\resources\\license.lic");
    }

    private LicenseParam initLicenseParam() {
        Preferences preferences = Preferences.userNodeForPackage(this.getClass());
        DefaultCipherParam cipherParam = new DefaultCipherParam(KEY_PASSWORD);
        DefaultKeyStoreParam keyStoreParam = new DefaultKeyStoreParam(this.getClass(), PRIVATE_KEY_STORE_PATH, ALIAS, STORE_PASSWORD, KEY_PASSWORD);
        return new DefaultLicenseParam("MyCompany-License", preferences, (KeyStoreParam)keyStoreParam, (CipherParam)cipherParam);
    }

    private LicenseContent createLicenseContent() {
        LicenseContent content = new LicenseContent();
        content.setSubject("MyCompany-License");
        content.setHolder(new X500Principal("CN=MyCompany, OU=Licensing, O=MyCompany, L=City, ST=State, C=CN"));
        content.setIssuer(new X500Principal("CN=MyCompany, OU=Licensing, O=MyCompany, L=City, ST=State, C=CN"));
        content.setIssued(new Date());
        content.setNotBefore(new Date());
        content.setNotAfter(new Date(System.currentTimeMillis() + 3650L * 24 * 60 * 60 * 1000)); // 10年有效期
        content.setConsumerType("User");
        content.setConsumerAmount(1);
        content.setInfo("This is a license for MyCompany.");
        return content;
    }
}

