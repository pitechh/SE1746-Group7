using AuthService.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace AuthService.Data.Configurations
{
    public class RoleConfiguration : IEntityTypeConfiguration<Role>
    {
        public void Configure(EntityTypeBuilder<Role> builder)
        {
            builder.Property(a => a.Name)
                .IsRequired()
                .HasMaxLength(100);

            builder.HasIndex(a => a.Name).IsUnique();
        }
    }
}
