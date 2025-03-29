using AuthService.Models;
using AuthService.Requests;
using AutoMapper;

namespace AuthService.Mappings
{
    public class MappingProfile : Profile
    {
        public MappingProfile() 
        {
            CreateMap<RegisterRequest, Account>();
        }
    }
}
