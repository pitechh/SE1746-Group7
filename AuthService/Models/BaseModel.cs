using System.ComponentModel.DataAnnotations.Schema;

namespace AuthService.Models
{
    public class BaseModel
    {
        [Column("id")]
        public long Id { get; set; }

        [Column("created_at")]        
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        
        [Column("updated_at")]
        public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

        [Column("deleted")]
        public Boolean Deleted { get; set; } = false;

        [Column("deleted_at")]
        public DateTime? DeletedAt { get; set; }

        public void MarkAsDeleted()
        {
            Deleted = true;
            DeletedAt = DateTime.UtcNow;
        }

        public void UpdateTimestamp()
        {
            UpdatedAt = DateTime.UtcNow;
        }
    }
}
