using AuthService.Data;
using AuthService.Helpers;
using AuthService.Models;
using AuthService.Requests;
using AuthService.Services;
using AutoMapper;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authentication.Google;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Claims;

namespace AuthService.Controllers
{
    [Route("api/auth")]
    [ApiController]
    public class AuthController : ControllerBase
    {
        private readonly IConfiguration _configuration;

        private readonly IMapper _mapper;
        private readonly ApplicationDbContext _context;
        private readonly JwtService _jwtService;

        public AuthController(IConfiguration configuration, IMapper mapper, 
            ApplicationDbContext context, JwtService jwtService)
        {
            _configuration = configuration;
            _mapper = mapper;
            _context = context;
            _jwtService = jwtService;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterRequest registerRequest)
        {
            Account account = _mapper.Map<Account>(registerRequest);
            if(account != null)
            {
                (string hash, string salt) = PasswordHelper.HashPassword(account.Password);
                //account.Password = null;
                account.PasswordHash = hash;
                account.PasswordSalt = salt;

                _context.Accounts.Add(account);
                _context.SaveChanges();

                return Ok();
            }
            return BadRequest(ModelState);
        }

        [HttpPost("registers")]
        public async Task<IActionResult> Registers([FromBody] List<RegisterRequest> registerRequests)
        {
            if (registerRequests == null || registerRequests.Count == 0)
            {
                return BadRequest("No data provided.");
            }

            List<Account> accounts = new();

            foreach (var registerRequest in registerRequests)
            {
                Account account = _mapper.Map<Account>(registerRequest);
                if (account != null)
                {
                    (string hash, string salt) = PasswordHelper.HashPassword(account.Password);
                    account.PasswordHash = hash;
                    account.PasswordSalt = salt;
                    accounts.Add(account);
                }
            }

            if (accounts.Count > 0)
            {
                await _context.Accounts.AddRangeAsync(accounts);
                await _context.SaveChangesAsync();
                return Ok();
            }

            return BadRequest("Failed to process registration.");
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginRequest loginRequest)
        {
            var account = await _context.Accounts.Include(a => a.Role).FirstOrDefaultAsync(a => a.Email == loginRequest.Email);
            if (account == null)
            {
                return Unauthorized("Invalid email or password.");
            }
            Boolean isAuthorized = PasswordHelper.VerifyPassword(loginRequest.Password, account.PasswordHash, account.PasswordSalt);
            if (!isAuthorized)
            {
                return Unauthorized("Invalid email or password.");
            }
            var token = _jwtService.GenerateAccessToken(account, account.Role.Name);
            return Ok(new { accessToken = token });

        }

        [HttpGet("login-google")]
        public IActionResult LoginWithGoogle()
        {
            var redirectUrl = Url.Action(nameof(GoogleResponse), "Auth", null, Request.Scheme);

            var properties = new AuthenticationProperties { RedirectUri = redirectUrl };
            return Challenge(properties, GoogleDefaults.AuthenticationScheme);
        }

        [HttpGet("google-response")]
        public async Task<IActionResult> GoogleResponse()
        {
            var result = await HttpContext.AuthenticateAsync(CookieAuthenticationDefaults.AuthenticationScheme);

            if (!result.Succeeded)
                return BadRequest("Google login failed");

            var claims = result.Principal?.Identities.FirstOrDefault()?.Claims;
            var email = claims?.FirstOrDefault(c => c.Type == ClaimTypes.Email)?.Value;
            if (string.IsNullOrEmpty(email))
                return BadRequest("Failed to retrieve email from Google");

            var account = await _context.Accounts.Include(a => a.Role).FirstOrDefaultAsync(a => a.Email == email);
            if (account == null)
            {
                return Unauthorized("Invalid email or password.");
            }

            // Tạo JWT Token
            var token = _jwtService.GenerateAccessToken(account, account.Role.Name);
            return Ok(new { Token = token });
        }
    }
}
