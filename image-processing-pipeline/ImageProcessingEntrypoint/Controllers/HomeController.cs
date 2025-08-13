using Microsoft.AspNetCore.Mvc;
using AppCore;
using System.Collections.Generic;
using System.IO;
using ImageProcessingEntrypoint;
using Microsoft.AspNetCore.Hosting;


namespace ImageProcessingPipeline.Controllers 
{
    [ApiController]
    [Route("api/filters")]
    public class HomeController : ControllerBase
    {
        private readonly string _filtersDir;
        public HomeController(IWebHostEnvironment env)
        {
            _filtersDir = Path.Combine(env.ContentRootPath,"..", "Filters");
        }
        
        [HttpGet]
        public IActionResult GetFilters()
        {
            Console.WriteLine($"\n\n\n\nFileeeeeeeee: {_filtersDir} ");
            var filters = FilterLoader.LoadAll(_filtersDir);
            int index = 0;

            var filterList = new List<object>
            {
                new
                {
                    id = index++,
                    name = "none",
                    label = "Fara filtru"
                }
            };

            foreach (var filter in filters)
            {
                var filterType = filter.GetType();
                var nameFilter = filterType.Name;
                var labelFilter = nameFilter;

                filterList.Add(new
                {
                    id = index++,
                    name = nameFilter,
                    label = labelFilter

                });

            }

            return Ok(new { data = filterList });
        }
    }
    
}