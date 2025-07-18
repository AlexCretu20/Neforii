import sys
from rich.prompt import Prompt
import pyfiglet
from ui import (
    render_logo, main_menu_panel, logged_in_menu_panel, login_panel, register_panel, logged_in_panel,
    feed_panel, post_panel, comments_panel, goodbye_panel, info, success, error, warning, vote_panel
)

# dummy data
users = {}
posts = [
    {
        'id': 1,
        'title': 'Welcome to Neforii CLI!',
        'text': 'This is a beautiful Reddit-like CLI app. Enjoy your stay!',
        'author': 'alex',
        'votes': 5,
        'comments': [
            {'author': 'user1', 'text': 'Love this!', 'votes': 2, 'replies': [
                {'author': 'user3', 'text': 'Me too!', 'votes': 1, 'replies': []}
            ]},
            {'author': 'user2', 'text': 'So cool!', 'votes': 0, 'replies': []}
        ]
    },
    {
        'id': 2,
        'title': 'What features do you want?',
        'text': 'Let us know what you would like to see in future updates.',
        'author': 'alex',
        'votes': 3,
        'comments': []
    }
]
votes = {}  # (username, post_id) or (username, 'comment', post_id, path): +1/-1

def login():
    login_panel()
    username = Prompt.ask("Username")
    password = Prompt.ask("Password", password=True)
    if username in users and users[username] == password:
        success(f"Welcome back, {username}!")
        return username
    else:
        error("Invalid credentials.")
        return None

def register():
    register_panel()
    while True:
        username = Prompt.ask("Choose a username")
        if username in users:
            error("Username already exists.")
        else:
            break
    password = Prompt.ask("Choose a password", password=True)
    users[username] = password
    success(f"User {username} registered!")
    return username

def show_feed(current_user=None):
    feed_panel(posts)
    post_id = Prompt.ask("Enter post ID to view, or blank to return", default="")
    if post_id.isdigit():
        post_id = int(post_id)
        post = next((p for p in posts if p['id'] == post_id), None)
        if post:
            view_post(post, current_user)

def view_post(post, current_user=None):
    while True:
        vote_panel(post, current_user, votes)
        flat_comments = comments_panel(post['comments'], votes, current_user, post['id'])
        if current_user:
            action = Prompt.ask(
                "Upvote (u), Downvote (d), Upvote Comment (uc), Downvote Comment (dc), Comment (c), Reply (r), Back (b)",
                choices=["u","d","uc","dc","c","r","b"])
            if action == "u":
                key = (current_user, post['id'])
                if votes.get(key) == 1:
                    warning("You already upvoted.")
                else:
                    if votes.get(key) == -1:
                        post['votes'] += 1  # remove previous downvote
                    post['votes'] += 1
                    votes[key] = 1
                    success("Upvoted!")
            elif action == "d":
                key = (current_user, post['id'])
                if votes.get(key) == -1:
                    warning("You already downvoted.")
                else:
                    if votes.get(key) == 1:
                        post['votes'] -= 1  # remove previous upvote
                    post['votes'] -= 1
                    votes[key] = -1
                    error("Downvoted!")
            elif action == "uc":
                if not flat_comments:
                    warning("No comments to upvote.")
                    continue
                idx = Prompt.ask("Enter comment number to upvote", default="1")
                if idx.isdigit():
                    idx = int(idx)
                    if 1 <= idx <= len(flat_comments):
                        c, parent_list, i, path = flat_comments[idx-1]
                        key = (current_user, 'comment', post['id'], path)
                        if votes.get(key) == 1:
                            warning("You already upvoted this comment.")
                        else:
                            if votes.get(key) == -1:
                                c['votes'] += 1  # remove previous downvote
                            c['votes'] += 1
                            votes[key] = 1
                            success("Comment upvoted!")
                    else:
                        warning("Invalid comment number.")
                else:
                    warning("Invalid input.")
            elif action == "dc":
                if not flat_comments:
                    warning("No comments to downvote.")
                    continue
                idx = Prompt.ask("Enter comment number to downvote", default="1")
                if idx.isdigit():
                    idx = int(idx)
                    if 1 <= idx <= len(flat_comments):
                        c, parent_list, i, path = flat_comments[idx-1]
                        key = (current_user, 'comment', post['id'], path)
                        if votes.get(key) == -1:
                            warning("You already downvoted this comment.")
                        else:
                            if votes.get(key) == 1:
                                c['votes'] -= 1  # remove previous upvote
                            c['votes'] -= 1
                            votes[key] = -1
                            error("Comment downvoted!")
                    else:
                        warning("Invalid comment number.")
                else:
                    warning("Invalid input.")
            elif action == "c":
                text = Prompt.ask("Enter your comment")
                post['comments'].append({'author': current_user, 'text': text, 'votes': 0, 'replies': []})
                success("Comment added!")
            elif action == "r":
                if not flat_comments:
                    warning("No comments to reply to.")
                    continue
                idx = Prompt.ask("Enter comment number to reply to", default="1")
                if idx.isdigit():
                    idx = int(idx)
                    if 1 <= idx <= len(flat_comments):
                        target_comment, parent_list, i, path = flat_comments[idx-1]
                        text = Prompt.ask("Enter your reply")
                        if 'replies' not in target_comment:
                            target_comment['replies'] = []
                        target_comment['replies'].append({'author': current_user, 'text': text, 'votes': 0, 'replies': []})
                        success("Reply added!")
                    else:
                        warning("Invalid comment number.")
                else:
                    warning("Invalid input.")
            elif action == "b":
                break
        else:
            Prompt.ask("Press Enter to go back")
            break

def main():
    import os
    current_user = None
    while True:
        os.system('cls' if sys.platform == 'win32' else 'clear')
        render_logo()
        if not current_user:
            main_menu_panel()
            choice = Prompt.ask("Choose an option", choices=["1","2","3"])
            if choice == "1":
                current_user = login()
            elif choice == "2":
                current_user = register()
            elif choice == "3":
                goodbye_panel()
                sys.exit(0)
        else:
            logged_in_panel(current_user)
            logged_in_menu_panel()
            choice = Prompt.ask("Choose an option", choices=["1","2","3"])
            if choice == "1":
                show_feed(current_user)
                Prompt.ask("Press Enter to return to menu")
            elif choice == "2":
                current_user = None
                info("Logged out.")
            elif choice == "3":
                goodbye_panel()
                sys.exit(0)

if __name__ == "__main__":
    main() 