using AuthService.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace AuthService.Data.Configurations
{
    public class AccountConfiguration : IEntityTypeConfiguration<Account>
    {
        public void Configure(EntityTypeBuilder<Account> builder)
        {
            builder.Property(a => a.Email)
                .IsRequired()
                .HasMaxLength(100);

            builder.Property(a => a.PasswordHash)
                .IsRequired()
                .HasMaxLength(255);

            builder.Property(a => a.FullName)
                .IsRequired()
                .HasMaxLength(100);

            builder.Property(a => a.Phone)
                .IsRequired()
                .HasMaxLength(11)
                .IsUnicode(false); // Đảm bảo chỉ chứa số

            builder.Property(a => a.Salary)
                .HasColumnType("decimal(18,2)") // Định dạng lương
                .HasDefaultValue(0);

            builder.Property(a => a.Image)
                .HasMaxLength(255);

            // Đảm bảo Email là duy nhất
            builder.HasIndex(a => a.Email).IsUnique();

            // Relationship với Role
            builder.HasOne(a => a.Role)
                .WithMany(r => r.Accounts)
                .HasForeignKey(a => a.RoleId)
                .OnDelete(DeleteBehavior.Restrict); // Không xóa Account khi xóa Role

            // Relationship với Department
            builder.HasOne(a => a.Department)
                .WithMany(d => d.Accounts)
                .HasForeignKey(a => a.DepartmentId)
                .OnDelete(DeleteBehavior.Cascade); // Xóa Account khi xóa Department
        }
    }
}
