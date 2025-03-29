using AuthService.Models;

namespace AuthService.Repositories.Interfaces
{
    public interface IAccountRepository
    {
        //Task<Account> GetByEmailAndPasswordAsync(string email, string password);

        Task<Account> GetByEmailAsync(string email);

        //Task<Account> Add(Account account);
    }
}
