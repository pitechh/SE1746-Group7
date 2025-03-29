using AuthService.Data;
using AuthService.Models;
using AuthService.Repositories.Interfaces;
using AuthService.Requests;
using Microsoft.EntityFrameworkCore;

namespace AuthService.Repositories
{
    public class AccountRepository : IAccountRepository
    {
        private readonly ApplicationDbContext _context;
        public AccountRepository(ApplicationDbContext context)
        {
            _context = context;
        }

        //public async Task<Account> GetByEmailAndPasswordAsync(string email, string password)
        //{
        //    return await _context.Accounts
        //        .FirstOrDefaultAsync(a => a.Email == email && a.Password == password);
        //}

        public async Task<Account> GetByEmailAsync(string email)
        {
            return await _context.Accounts.FirstOrDefaultAsync(a => a.Email == email);
        }

        //public async Task<Account> Add(Account account)
        //{
        //    return await _context.Accounts.Add(account);
        //}
    }
}
