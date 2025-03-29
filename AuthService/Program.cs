using AuthService.Data;
using AuthService.Helpers;
using AuthService.Services;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authentication.Google;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Steeltoe.Discovery.Client;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddDbContext<ApplicationDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));

var googleSettings = builder.Configuration.GetSection("Google");
var rsaPublicKey = RsaKeyHelper.LoadPublicKey(builder.Configuration["JwtSettings:PublicKey"]);

builder.Services.AddAuthentication(options =>
{
    options.DefaultScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = GoogleDefaults.AuthenticationScheme;
    options.DefaultSignInScheme = CookieAuthenticationDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = true,
        ValidateAudience = true,
        ValidateLifetime = true,
        ValidateIssuerSigningKey = true,
        IssuerSigningKey = new RsaSecurityKey(rsaPublicKey),
        ClockSkew = TimeSpan.Zero
    };
})
.AddCookie(CookieAuthenticationDefaults.AuthenticationScheme)
.AddGoogle(GoogleDefaults.AuthenticationScheme, options =>
{
    options.ClientId = googleSettings["ClientId"];
    options.ClientSecret = googleSettings["ClientSecret"];

    options.CorrelationCookie.SameSite = SameSiteMode.Lax; // Hoặc None nếu dùng HTTPS
    options.CorrelationCookie.SecurePolicy = CookieSecurePolicy.Always;    
});

builder.Services.Configure<CookiePolicyOptions>(options =>
{
    options.MinimumSameSitePolicy = SameSiteMode.None;
    options.CheckConsentNeeded = context => false;
    options.Secure = CookieSecurePolicy.Always;
});

builder.Services.AddAuthorization();

builder.Services.AddAutoMapper(typeof(Program));
// Add services to the container.

builder.Services.AddSingleton<JwtService>();

//builder.Services.AddDiscoveryClient();

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAllOrigins",
        policy => policy.AllowAnyOrigin()
                        .AllowAnyMethod()
                        .AllowAnyHeader());
});



var app = builder.Build();

app.UseCors("AllowAllOrigins"); // Bật CORS

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

//app.UseDiscoveryClient();

app.UseCookiePolicy();

app.UseAuthentication();

app.UseAuthorization();

app.MapControllers();

app.Run();
