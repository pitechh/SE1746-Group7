using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;

namespace AuthService.Requests
{
    public class RegisterRequest
    {
        public string Code { get; set; }

        public string Email { get; set; }

        public string PersonalEmail { get; set; }

        public string Fullname { get; set; }

        public string Password { get; set; }

        public string Phone { get; set; }

        public decimal Salary { get; set; }

        public string? Image { get; set; } = "";

        public long RoleId { get; set; }

        public long DepartmentId { get; set; }
    }
}
