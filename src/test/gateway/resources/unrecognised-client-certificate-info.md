[unrecognised-client-certificate.pfx](unrecognised-client-certificate.pfx) is a key store containing a test
SSL client certificate (and the corresponding private key) that should not be recognised by Rhubarb's API (gateway).
The purpose of this key store is to be used in tests, in order to verify
that HTTPS requests with this certificate are rejected. Here's how it was created:

```bash
openssl genrsa 2048 > private.pem
openssl req -x509 -new -key private.pem -out cert.pem -days 36500
openssl pkcs12 -export -in cert.pem -inkey private.pem -out unrecognised-client-certificate.pfx -noiter -nomaciter
```

The key store was created with password `testcert`.
