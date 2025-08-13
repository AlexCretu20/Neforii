using Microsoft.AspNetCore.Mvc;

namespace ImageProcessingPipeline.Controllers
{
    [ApiController]
    [Route("api/hello")]
    public class HelloController : ControllerBase
    {
        [HttpGet]
        public IActionResult GetHello()
        {
            return Ok(new { message = "Hello World!" });
        }
    }
}