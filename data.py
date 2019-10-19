import pymongo
db = pymongo.MongoClient()['steam']

def iterate_users(query=None):
    if query is None:
        query = {}
    for user in db.users.find(query, {"_id": False}):
        yield(user)
