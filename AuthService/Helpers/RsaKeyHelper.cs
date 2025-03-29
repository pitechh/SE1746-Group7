using System.Security.Cryptography;

namespace AuthService.Helpers
{
    public class RsaKeyHelper
    {
        public static RSA LoadPrivateKey(string base64Key)
        {
            byte[] privateKeyBytes = Convert.FromBase64String(base64Key);
            RSA rsa = RSA.Create();
            rsa.ImportPkcs8PrivateKey(privateKeyBytes, out _);
            return rsa;
        }

        public static RSA LoadPublicKey(string base64Key)
        {
            byte[] publicKeyBytes = Convert.FromBase64String(base64Key);
            RSA rsa = RSA.Create();
            rsa.ImportSubjectPublicKeyInfo(publicKeyBytes, out _);
            return rsa;
        }
    }
}
