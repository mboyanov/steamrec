from train_test_split import is_excluded, split_user


def test_should_exclude_users_with_outlier_number_of_games__small():
    # GIVEN
    user = {
        'games': [1, 2, 3, 4]
    }
    # WHEN
    # THEN
    assert is_excluded(user)


def test_should_exclude_users_with_outlier_number_of_games__big():
    # GIVEN
    user = {
        'games': [1] * 2000
    }
    # WHEN
    # THEN
    assert is_excluded(user)


def test_should_allow_users_with_normal_number_of_games():
    # GIVEN
    user = {
        "games": [1, 2, 3, 4, 5]
    }
    # WHEN
    # THEN
    assert not is_excluded(user)


def test_should_split_user():
    # GIVEN
    user = {'games':
            [{'name': x} for x in ["Warcraft", "Warcraft II", "Warcraft III", "World of Warcraft", "Dota", "Dota 2"]]}
    # WHEN
    split_user(user)
    # THEN
    assert len(user['train_games']) == 5
    assert len(user['valid_games']) == 1
