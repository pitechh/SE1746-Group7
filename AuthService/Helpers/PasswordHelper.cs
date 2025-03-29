using System.Security.Cryptography;
using System.Text;

namespace AuthService.Helpers
{
    public class PasswordHelper
    {
        public static (string hash, string salt) HashPassword(string password)
        {
            // Tạo Salt ngẫu nhiên
            byte[] saltBytes = new byte[16];
            using (var rng = new RNGCryptoServiceProvider())
            {
                rng.GetBytes(saltBytes);
            }
            string salt = Convert.ToBase64String(saltBytes);

            // Băm mật khẩu với Salt
            string hash = ComputeHash(password, salt);
            return (hash, salt);
        }

        private static string ComputeHash(string password, string salt)
        {
            using (var sha256 = SHA256.Create())
            {
                byte[] inputBytes = Encoding.UTF8.GetBytes(password + salt);
                byte[] hashBytes = sha256.ComputeHash(inputBytes);
                return Convert.ToBase64String(hashBytes);
            }
        }

        public static bool VerifyPassword(string password, string storedHash, string storedSalt)
        {
            string computedHash = ComputeHash(password, storedSalt);
            return computedHash == storedHash;
        }
    }
}
