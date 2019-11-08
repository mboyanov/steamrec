package com.steamrec

case class User(user_id: BigInt, games: List[Game], train_games: List[Game], valid_games: List[Game])
