package com.crypto.cadp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Collections;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OutputEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import com.crypto.cadp.pojo.EncryptRequest;
import com.crypto.cadp.pojo.EncryptResponse;
import com.crypto.cadp.pojo.ProtectRequest;
import com.crypto.cadp.pojo.ProtectResponse;
import com.crypto.cadp.service.NaeSessionService;
import com.ingrian.security.nae.NAEKey;
import com.ingrian.security.nae.NAESession;

@SpringBootApplication
public class CadpApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(CadpApplication.class);
	
	@Autowired
	private NaeSessionService naeSessionService;

	public static void main(String[] args) {
		SpringApplication.run(CadpApplication.class, args);
		LOG.info("APPLICATION FINISHED");
	}

	@Override
	public void run(String... args) throws Exception {
		naeSessionService.createSession();
		
		System.out.println("Using BouncyCastle =====> " + (encryptDataBC("1234-5678-9012-3456".getBytes())).toString());
		System.out.println("Using CADP for WS  =====> " + encryptDataCadpWS("1234-5678-9012-3456"));
		System.out.println("Using CRDP         =====> " + encryptDataCRDP("1234-5678-9012-3456"));
		System.out.println("Using CADP         =====> " + encryptDataCADPJava("1234-5678-9012-3456"));
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
        return restTemplate;
	}

	public byte[] encryptDataBC(byte[] data) throws CertificateEncodingException, CMSException, IOException, Exception {
		// Generated the PKCS#12 file using the OpenSSL commands
		Security.addProvider(new BouncyCastleProvider());
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");

		File certificateFile = ResourceUtils.getFile("classpath:sample.cer");
		X509Certificate certificate = (X509Certificate) certFactory
				.generateCertificate(new FileInputStream(certificateFile));

		KeyStore keystore = KeyStore.getInstance("PKCS12");
		File pkcs12File = ResourceUtils.getFile("classpath:sample.p12");
		keystore.load(new FileInputStream(pkcs12File), "root".toCharArray());

		byte[] encryptedData = null;
		if (null != data && null != certificate) {
			CMSEnvelopedDataGenerator cmsEnvelopedDataGenerator = new CMSEnvelopedDataGenerator();

			JceKeyTransRecipientInfoGenerator jceKey = new JceKeyTransRecipientInfoGenerator(certificate);
			cmsEnvelopedDataGenerator.addRecipientInfoGenerator(jceKey);
			CMSTypedData msg = new CMSProcessableByteArray(data);
			OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC).setProvider("BC")
					.build();
			CMSEnvelopedData cmsEnvelopedData = cmsEnvelopedDataGenerator.generate(msg, encryptor);
			encryptedData = cmsEnvelopedData.getEncoded();
		}
		return encryptedData;
		// PrivateKey key = (PrivateKey) keystore.getKey("aj", keyPassword);
	}

	public String encryptDataCadpWS(String data) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		// Prepare headers
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		String url = "http://192.168.2.221:8080/protectappws/services/rest/encrypt";
		
		EncryptRequest request = new EncryptRequest();
        EncryptRequest.Encrypt encrypt = new EncryptRequest.Encrypt();
        encrypt.setUsername("admin");
        encrypt.setPassword("ChangeIt01!");
        encrypt.setKeyname("cadp");
        encrypt.setKeyiv("12345678123456781234567812345678");
        encrypt.setTransformation("AES/CBC/PKCS5Padding");
        encrypt.setPlaintext("4592-3522-9633-3835");
        request.setEncrypt(encrypt);
        
        HttpEntity<EncryptRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<EncryptResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, EncryptResponse.class);
		return response.getBody().getEncryptResponse().getCipherText();
	}

	public String encryptDataCRDP(String data) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		// Prepare headers
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		String url = "http://192.168.2.221:32000/v1/protect";
		
		ProtectRequest request = new ProtectRequest();
		request.setData(data);
		request.setPolicyName("demo");
        
        HttpEntity<ProtectRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ProtectResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, ProtectResponse.class);
		return response.getBody().getCipherText();
	}

	public String encryptDataCADPJava(String data) throws Exception {
		NAESession session = naeSessionService.getSession();
		String algo = "AES/CBC/PKCS5Padding";
		String cipherText = "";
		
		try {
			//Creates the IvParameterSpec object
			IvParameterSpec ivSpec = new IvParameterSpec("1234567812345678".getBytes());
			
			//Gets public key to encrypt data (just a key handle , key data does not leave the Key Manager)
			NAEKey key = NAEKey.getSecretKey("cadp", session);
			
			//Creates a encryption cipher
			Cipher encryptCipher = Cipher.getInstance(algo, "IngrianProvider");
			
			//Initializes the cipher to encrypt the data
			encryptCipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
			
			//Encrypt date of birth
			cipherText = encryptCipher.doFinal(data.getBytes()).toString();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return cipherText;
	}
}
