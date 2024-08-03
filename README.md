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

## Using CADP for Java

## Using CADP Web Service

## Using CRDP
