import json
import random
from collections import Counter
from data import iterate_users
import logging
logging.basicConfig(level=logging.INFO)
random.seed(1402)


def is_excluded(user):
    num_games = len(user['games'])
    if num_games < 5 or num_games > 1500:
        return True


def split_user(user, eligible_games: set = None):
    if eligible_games is not None:
        user['games'] = [g for g in user['games'] if g['name'] in eligible_games]
    num_games = len(user['games'])
    num_valid_games = num_games // 5
    valid_games = random.sample(user['games'], k=num_valid_games)
    valid_games_names = set(x['name'] for x in valid_games)
    train_games = [x for x in user['games'] if x['name'] not in valid_games_names]
    user['valid_games'] = valid_games
    user['train_games'] = train_games


def get_eligible_games():
    game_counts = Counter()
    for u in iterate_users():
        if is_excluded(u):
            continue
        for g in u['games']:
            game_counts[g['name']] += 1
    return set(x for x, count in game_counts.items() if count >= 50)


if __name__ == '__main__':
    train_games = set()
    valid_games = set()
    logging.info("Getting eligible games")
    eligible_games = get_eligible_games()
    logging.info(f"Found {len(eligible_games)} eligible games")
    logging.info("Splitting users")
    users = []
    for user in iterate_users():
        if is_excluded(user):
            continue
        split_user(user, eligible_games)
        users.append(user)
        for g in user['train_games']:
            train_games.add(g['name'])
        for g in user['valid_games']:
            valid_games.add(g['name'])

    if len(valid_games.difference(train_games)) != 0:
        assert False, "Games in validation need to be present in testing as well. Difference:" + str(valid_games.difference(train_games))
    with open('train-test-split.json', 'w') as out:
        for u in users:
            out.write(json.dumps(u)+"\n")
