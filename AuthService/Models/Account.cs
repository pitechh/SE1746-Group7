
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace AuthService.Models
{
    [Table("account")]
    public class Account : BaseModel
    {
        [Column("code")]
        public string Code { get; set; }

        [Column("email")]
        public string Email { get; set; }

        [Column("personal_email")]
        public string PersonalEmail { get; set; }

        //[RegularExpression(@"^(?=.*\d).{6,}$", ErrorMessage = "Password must be at least 6 characters and contain at least one number.")]
        [Column("password")]
        public string? Password { get; set; }

        [Column("password_hash")]
        public string PasswordHash { get; set; }

        [Column("password_salt")]
        public string PasswordSalt { get; set; }

        //[Required(ErrorMessage = "Full name is required.")]
        [Column("fullname")]
        public string FullName { get; set; }

        [Column("phone")]
        //[RegularExpression(@"^\d{11}$", ErrorMessage = "Phone number must be exactly 11 digits.")]
        public string Phone { get; set; }

        [Column("salary")]
        public decimal Salary { get; set; } = 0;

        [Column("image")]
        public string? Image {  get; set; }

        [Column("role_id")]
        public long RoleId { get; set; }

        [Column("department_id")]
        public long DepartmentId { get; set; }

        public Role? Role { get; set;}
        public Department? Department { get; set; }
    }
}
