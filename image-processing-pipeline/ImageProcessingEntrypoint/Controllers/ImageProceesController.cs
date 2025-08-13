using System.Reflection;
using ImageProcessingEntrypoint;
using Microsoft.AspNetCore.Mvc;

namespace ImageProcessingPipeline.Controllers;

public class ImageProceesController : ControllerBase
{
    
    [HttpPost("applyFilter")]
    // ne cere id-ul
    public IActionResult ApplyFilter([FromForm] IFormFile image, [FromQuery] int filterId)
    {
        if (image == null || image.Length == 0)
        {
            return BadRequest("No image uploaded.");
        }

        var filters = FilterLoader.LoadAll(PathConfig.FiltersDir);
        var tempPath = Path.Combine(Path.GetTempPath(), Path.GetRandomFileName());
        using (var stream = new FileStream(tempPath, FileMode.Create))
        {
            image.CopyTo(stream);

        }

        var resultBytes = ImageProcessingService.ApplyFilter(tempPath, filterId, filters);

        return File(resultBytes, "image/png");
    }
}