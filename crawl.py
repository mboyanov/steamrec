import time

import users
import steam_api
import logging
FORMAT = '%(asctime)-15s %(message)s'
logging.basicConfig(format=FORMAT, level=logging.INFO)


def enqueue_friends(friends):
    for f in friends.get('friendslist', {}).get('friends',[]):
        f['steam_id'] = f['steamid']
        if not users.exists(f) and not users.is_enqueued(f):
            users.enqueue(f['steamid'])


if __name__ == '__main__':
    user = users.get_uncrawled_user()
    steps = 0
    while user is not None and steps < 50000:
        logging.info(f"Starting crawl for {user} at step {steps}")
        steam_id = user['steam_id']
        friends = steam_api.crawl_user(steam_id)
        games = steam_api.get_user_games(steam_id)
        # TODO: index
        enqueue_friends(friends)

        if 'games' in games['response']:
            games = games['response']['games']

            user = {
                "steam_id": steam_id,
                "games": games
            }
            users.insert_user(user)
        else:
            logging.info("No games, skipping")
        users.dequeue(steam_id)
        user = users.get_uncrawled_user()
        steps += 1
        time.sleep(5)