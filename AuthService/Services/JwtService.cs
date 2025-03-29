using AuthService.Helpers;
using AuthService.Models;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;

namespace AuthService.Services
{
    public class JwtService
    {
        private readonly IConfiguration _configuration;
        private readonly RSA _privateKey;
        private readonly RSA _publicKey;
        private readonly int _tokenExpiration;

        public JwtService(IConfiguration configuration)
        {
            _configuration = configuration;

            string privateKeyString = _configuration["JwtSettings:PrivateKey"];
            string publicKeyString = _configuration["JwtSettings:PublicKey"];
            _privateKey = RsaKeyHelper.LoadPrivateKey(privateKeyString);
            _publicKey = RsaKeyHelper.LoadPublicKey(publicKeyString);
            _tokenExpiration = int.Parse(_configuration["JwtSettings:AccessTokenExpirationSeconds"]);
        }

        public string GenerateAccessToken(Account account, string userRoles)
        {
            var now = DateTime.UtcNow;
            var expiration = now.AddSeconds(_tokenExpiration);

            var claims = new[]
            {
                new Claim(JwtRegisteredClaimNames.Sub, account.Code),
                new Claim("authorities", userRoles),
                new Claim("authorieIds", account.RoleId.ToString()),
                new Claim("accountId", account.Id.ToString()),
                new Claim(JwtRegisteredClaimNames.Iat, new DateTimeOffset(now).ToUnixTimeSeconds().ToString(), ClaimValueTypes.Integer64)
            };

            var credentials = new SigningCredentials(new RsaSecurityKey(_privateKey), SecurityAlgorithms.RsaSha256);

            var token = new JwtSecurityToken(
                claims: claims,
                notBefore: now,
                expires: expiration,
                signingCredentials: credentials
            );

            return new JwtSecurityTokenHandler().WriteToken(token);
        }
    }
}
