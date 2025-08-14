using Microsoft.AspNetCore.Mvc;
using ImageProcessingEntrypoint.Services;
using AppCore;

namespace ImageProcessingPipeline.Controllers
{
    [ApiController]
    [Route("api/image")]
    public class ImageProceesController : ControllerBase
    {
        [HttpPost("applyFilter")]
        public async Task<IActionResult> ApplyFilter([FromForm] IFormFile image, [FromQuery] int filterId)
        {
            if (image is null || image.Length == 0)
                return BadRequest("No image uploaded.");

            var filters = FilterService.GetFilters()
                                       .Select(f => f.Instance)
                                       .ToList();

            var tempPath = Path.Combine(Path.GetTempPath(), Path.GetRandomFileName());
            await using (var stream = System.IO.File.Create(tempPath))
            {
                await image.CopyToAsync(stream);
            }

            try
            {
                var resultBytes = await ImageProcessingEntrypoint.ImageProcessingService
                    .ApplyFilterAsync(tempPath, filterId, filters);

                return File(resultBytes, "image/png");
            }
            finally
            {
                try { System.IO.File.Delete(tempPath); } catch { /* ignore */ }
            }
        }
    }
}

