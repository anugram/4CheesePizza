# 4CheesePizza
Four ways to encrypt data

## Different cheese, different flavor
| Bouncy Castle | CADP for Java | CADP for Java WS | CRDP |
| ----------- | ----------- | ----------- | ----------- |
| Open Source and free to use | Proprietary Software (Java SDK) | Proprietary Software (XML/REST API) | Proprietary Software (REST API) |
| Key generation local to file system | Key generated on CipherTrust manager and never leaves | Key generated on CipherTrust manager and never leaves | Key generated on CipherTrust manager and never leaves |
| Full crypto knowledge needed including keygen and crypto algorithms | Partial knowledge required, key generation is outside | Partial knowledge required, key generation is outside | No crypto knowledge required, key generation, policy and algorithm configuration outside |
| Need to be bundled as an SDK in the application build | Need to be bundled as an SDK in the application build | Not needed, just API call | Not needed, just API call |
| Not runnable, SDK | Not runnable, SDK | Runs on a Tomcat server | Runs in container |
| Change in algo means new build, retest, deploy again | Change in algo means new build, retest, deploy again | Change in algo means new build, retest, deploy again | Changes happen on CM, pulled by CRDP itself, no new build |

## Bouncy Castle
```
// Add Bouncy Castle as the Java Security Provider
Security.addProvider(new BouncyCastleProvider());
CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");

// This assumes that the certificate and the PKCS12 file are already generated and are available to be used in the code
// This exposes the risk of private key being available in plain on the file system where the below Java code is running
File certificateFile = ResourceUtils.getFile("classpath:sample.cer");
X509Certificate certificate = (X509Certificate) certFactory
    .generateCertificate(new FileInputStream(certificateFile));

// Both certificate and the P12 file are now loaded in memory for the Java code to use
KeyStore keystore = KeyStore.getInstance("PKCS12");
File pkcs12File = ResourceUtils.getFile("classpath:sample.p12");
keystore.load(new FileInputStream(pkcs12File), "root".toCharArray());

byte[] encryptedData = null;
if (null != data && null != certificate) {
  CMSEnvelopedDataGenerator cmsEnvelopedDataGenerator = new CMSEnvelopedDataGenerator();

  JceKeyTransRecipientInfoGenerator jceKey = new JceKeyTransRecipientInfoGenerator(certificate);
  cmsEnvelopedDataGenerator.addRecipientInfoGenerator(jceKey);
  CMSTypedData msg = new CMSProcessableByteArray(data);

  // This would require the developer to have an understanding of the Crypto algo i.e. AES128_CBC in this case
  // Any update to algo will require the code to be rebuilt
  OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC).setProvider("BC")
      .build();
  CMSEnvelopedData cmsEnvelopedData = cmsEnvelopedDataGenerator.generate(msg, encryptor);
  encryptedData = cmsEnvelopedData.getEncoded();
}
return encryptedData;
```

## Using CADP for Java

## Using CADP Web Service

## Using CRDP
