import pymongo


class MongoFactory:
    INSTANCE = None

    @staticmethod
    def get():
        if MongoFactory.INSTANCE is None:
            MongoFactory.INSTANCE = pymongo.MongoClient()
        return MongoFactory.INSTANCE["steam"]


def get_uncrawled_user():
    return MongoFactory.get()['queue'].find_one({})


def dequeue(steam_id):
    MongoFactory.get()['queue'].remove({"steam_id": steam_id})


def enqueue(steam_id):
    MongoFactory.get()['queue'].insert({"steam_id": steam_id})


def insert_user(u):
    MongoFactory.get()['users'].insert(u)


def exists(u):
    return MongoFactory.get()['users'].find_one({"steam_id": u['steam_id']})

def is_enqueued(u):
    return MongoFactory.get()['queue'].find_one({"steam_id": u['steam_id']}) is not None