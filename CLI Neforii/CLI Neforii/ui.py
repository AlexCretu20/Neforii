from rich.console import Console
from rich.panel import Panel
from rich.table import Table
from rich.text import Text
import pyfiglet

console = Console()

def render_logo():
    logo = pyfiglet.figlet_format('Neforii', font='slant')
    console.print(logo, style='bold cyan')
    console.rule("[bold magenta]Reddit-like CLI App[/bold magenta]")

def main_menu_panel():
    panel = Panel("[bold blue]1.[/bold blue] Login\n[bold magenta]2.[/bold magenta] Register\n[bold red]3.[/bold red] Exit", title="[bold]Main Menu[/bold]", style="bold white")
    console.print(panel)

def logged_in_menu_panel():
    panel = Panel("[bold green]1.[/bold green] Show Feed\n[bold yellow]2.[/bold yellow] Logout\n[bold red]3.[/bold red] Exit", title="[bold]User Menu[/bold]", style="bold white")
    console.print(panel)

def login_panel():
    console.print(Panel("[bold cyan]Login[/bold cyan]", style="cyan"))

def register_panel():
    console.print(Panel("[bold magenta]Register[/bold magenta]", style="magenta"))

def logged_in_panel(username):
    console.print(Panel(f"[green]Logged in as {username}[/green]", style="green"))

def feed_panel(posts):
    console.print(Panel("[bold blue]Feed[/bold blue]", style="blue"))
    table = Table(show_header=True, header_style="bold magenta")
    table.add_column("ID", style="dim", width=4)
    table.add_column("Title")
    table.add_column("Author", style="cyan")
    table.add_column("Votes", style="green")
    for post in posts:
        table.add_row(str(post['id']), post['title'], post['author'], str(post['votes']))
    console.print(table)

def vote_panel(post, current_user, votes):
    key = (current_user, post['id']) if current_user else None
    user_vote = votes.get(key) if key else None
    up_style = "bold green" if user_vote == 1 else "green"
    down_style = "bold red" if user_vote == -1 else "red"
    vote_count = post['votes']
    up_arrow = f"[" + up_style + "]↑[/]"
    down_arrow = f"[" + down_style + "]↓[/]"
    panel = Panel(f"Upvotes\n{up_arrow} [bold]{vote_count}[/bold] {down_arrow}", border_style="magenta", style="white", width=20)
    console.print(panel)

def post_panel(post):
    body = Text(post.get('text', ''), style="white")
    panel = Panel(
        body,
        title=f"[bold]{post['title']}[/bold]",
        subtitle=f"by [cyan]{post['author']}[/cyan] | Votes: [green]{post['votes']}[/green]",
        style="white",
        border_style="magenta"
    )
    console.print(panel)

def comments_panel(comments, votes, current_user, post_id, show_numbers=True, indent=0, idx_offset=1, _flat=None, _path=()):

    if _flat is None:
        _flat = []
        console.print("[yellow]Comments:[/yellow]")
    for i, c in enumerate(comments):
        idx = idx_offset + len(_flat)
        prefix = "    " * indent
        number = f"[{idx}] " if show_numbers else ""
        path = _path + (i,)
        key = (current_user, 'comment', post_id, path) if current_user else None
        user_vote = votes.get(key) if key else None
        up_style = "bold green" if user_vote == 1 else "green"
        down_style = "bold red" if user_vote == -1 else "red"
        vote_count = c.get('votes', 0)
        up_arrow = f"[" + up_style + "]↑[/]"
        down_arrow = f"[" + down_style + "]↓[/]"
        vote_str = f"{up_arrow} [bold]{vote_count}[/bold] {down_arrow}"
        console.print(f"{prefix}{number}{vote_str} [cyan]{c['author']}[/cyan]: {c['text']}")
        _flat.append((c, comments, i, path))
        if c.get('replies'):
            comments_panel(c['replies'], votes, current_user, post_id, show_numbers, indent+1, idx_offset, _flat, path)
    return _flat

def goodbye_panel():
    console.print("[bold red]Goodbye![/bold red]")

def info(msg):
    console.print(f"[blue]{msg}[/blue]")

def success(msg):
    console.print(f"[green]{msg}[/green]")

def error(msg):
    console.print(f"[red]{msg}[/red]")

def warning(msg):
    console.print(f"[yellow]{msg}[/yellow]") 