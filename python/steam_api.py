import requests
from simplejson import JSONDecodeError

from config import STEAM_API_KEY
import logging
FRIENDS_URL = "http://api.steampowered.com/ISteamUser/GetFriendList/v0001/"

GAMES_URL = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/"

headers = {
    'cache-control': "no-cache",
}


def crawl_user(steamid):
    querystring = {"key": STEAM_API_KEY, "steamid": steamid, "relationship": "friend"}
    headers = {
        'cache-control': "no-cache",
    }
    response = requests.request("GET", FRIENDS_URL, headers=headers, params=querystring)
    try:
        return response.json()
    except JSONDecodeError as e:
        print(response.text)
        raise e


def get_user_games(steamid):
    querystring = {"key": STEAM_API_KEY, "steamid": steamid, "format": "json",
                   "include_appinfo": "1", "include_played_free_games": "1"}
    response = requests.request("GET", GAMES_URL, headers=headers, params=querystring)
    try:
        return response.json()
    except JSONDecodeError as e:
        logging.error(f"Could not decode json: {response.text}")
        raise e

if __name__ == '__main__':
    print(crawl_user("76561198046794970"))
    print(get_user_games("76561198046794970"))