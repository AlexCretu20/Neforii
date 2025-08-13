using Microsoft.AspNetCore.Mvc;
using ImageProcessingEntrypoint.Services;



namespace ImageProcessingPipeline.Controllers 
{
    [ApiController]
    [Route("api/filters")]
    public class HomeController : ControllerBase
    {
        
        [HttpGet]
        public IActionResult GetFilters()
        {
            var filterList = FilterService.GetFilters()
                .Select(f => new
                {
                    id = f.Id,
                    name = f.Name,
                    label = f.Label

                });
            
            return Ok(new { data = filterList });
        }
        
    }
    
}