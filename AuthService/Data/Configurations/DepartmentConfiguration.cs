using AuthService.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace AuthService.Data.Configurations
{
    public class DepartmentConfiguration : IEntityTypeConfiguration<Department>
    {
        public void Configure(EntityTypeBuilder<Department> builder)
        {
            builder.Property(a => a.Code)
                .IsRequired()
                .HasMaxLength(255);

            builder.Property(a => a.Name)
                .IsRequired()
                .HasMaxLength(100);

            builder.HasIndex(a => a.Code).IsUnique();
        }
    }
}
