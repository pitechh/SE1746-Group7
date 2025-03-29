using System.ComponentModel.DataAnnotations.Schema;

namespace AuthService.Models
{
    [Table("role")]
    public class Role
    {
        [Column("id")]
        public long Id { get; set; }

        [Column("name")]
        public string Name { get; set; }

        public ICollection<Account> Accounts { get; set; }
    }
}
