using Microsoft.AspNetCore.Server.HttpSys;
using System.ComponentModel.DataAnnotations.Schema;

namespace AuthService.Models
{
    [Table("department")]
    public class Department : BaseModel
    {
        [Column("code")]
        public string Code { get; set; }

        [Column("name")]
        public string Name { get; set; }

        [Column("number_of_member")]
        public int NumberOfMember {  get; set; }

        public ICollection<Account> Accounts { get; set; }
    }
}
