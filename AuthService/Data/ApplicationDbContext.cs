using AuthService.Data.Configurations;
using AuthService.Models;
using Microsoft.EntityFrameworkCore;

namespace AuthService.Data
{
    public class ApplicationDbContext : DbContext
    {
        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options)
        : base(options) { }

        public DbSet<Account> Accounts { get; set; }    
        public DbSet<Role> Roles { get; set; }  
        public DbSet<Department> Departments { get; set; }

        protected override void OnModelCreating(ModelBuilder builder)
        {
            builder.ApplyConfiguration(new AccountConfiguration());
        }
    }
}
