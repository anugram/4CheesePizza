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
// Bouncy Castle library added to POM, build will include the Bouncy Castle library
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
```
// CADP library added to POM, build will include the CADP library
NAESession session = naeSessionService.getSession();
// This would require the developer to have an understanding of the Crypto algo i.e. AES/CBC/PKCS5Padding in this case
// Any update to algo will require the code to be rebuilt
String algo = "AES/CBC/PKCS5Padding";
String cipherText = "";
try {
    // Developer should have deeper understanding of crypto like what IV is and what should be the value
    IvParameterSpec ivSpec = new IvParameterSpec("1234567812345678".getBytes());
    
    // Gets public key from CipherTrust Manager to encrypt data (just a key handle , key data does not leave the Key Manager)
    NAEKey key = NAEKey.getSecretKey("KeyName", session);
    
    // Creates a encryption cipher
    Cipher encryptCipher = Cipher.getInstance(algo, "IngrianProvider");
    
    // Initializes the cipher to encrypt the data
    encryptCipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
    
    //Encrypt data
    cipherText = encryptCipher.doFinal(data.getBytes()).toString();
} catch(Exception ex) {
    ex.printStackTrace();
}
return cipherText;
```

## Using CADP Web Service
```
// No additional library needs to be bundled into the code
RestTemplate restTemplate = new RestTemplate();
// Prepare headers
HttpHeaders headers = new HttpHeaders();
headers.set("Content-Type", "application/json");
String url = "http://<CADP_WS_IP>:<CADP_WS_PORT>/protectappws/services/rest/encrypt";

EncryptRequest request = new EncryptRequest();
EncryptRequest.Encrypt encrypt = new EncryptRequest.Encrypt();
encrypt.setUsername("<CM_Username>");
encrypt.setPassword("<CM_Password>");
encrypt.setKeyname("KeyName");
// This would require the developer to have an understanding of the Crypto concepts like IV and algo i.e. AES/CBC/PKCS5Padding in this case
// Any update to algo will require the code to be rebuilt
encrypt.setKeyiv("12345678123456781234567812345678");
encrypt.setTransformation("AES/CBC/PKCS5Padding");

encrypt.setPlaintext("4592-3522-9633-3835");
request.setEncrypt(encrypt);

HttpEntity<EncryptRequest> entity = new HttpEntity<>(request, headers);
ResponseEntity<EncryptResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, EncryptResponse.class);
return response.getBody().getEncryptResponse().getCipherText();
```

## Using CRDP
```
// No additional library needs to be bundled into the code
// No knowledge needed for IV, algo, key creation and management
// Just a simple API call

RestTemplate restTemplate = new RestTemplate();
// Prepare headers
HttpHeaders headers = new HttpHeaders();
headers.set("Content-Type", "application/json");
String url = "http://<CRDP_IP>:<CRDP_PORT>/v1/protect";

ProtectRequest request = new ProtectRequest();
request.setData(data);
request.setPolicyName("demo");

HttpEntity<ProtectRequest> entity = new HttpEntity<>(request, headers);
ResponseEntity<ProtectResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, ProtectResponse.class);
return response.getBody().getCipherText();
```
