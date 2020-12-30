import requests

requests.post("http://127.0.0.1:9089/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@gmail.com"})
requests.post("http://127.0.0.1:9089/create/user", data={"username": "Julia", "password": "tddd80", "email": "test2@test.se"})
r = requests.post("http://127.0.0.1:9089/user/login", data={"username": "Kiana", "password": "tddd80"})
token = r.json()["token"]
header = {"Authorization": "Bearer " + token}
requests.post('http://127.0.0.1:9089/messages/send', data={'message': 'hej', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
requests.post('http://127.0.0.1:9089/messages/send', data={'message': 'DU', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
r = requests.post("http://127.0.0.1:9089/mark/2/read/Julia", headers=header)
print(r.status_code)
r = requests.get("http://127.0.0.1:9089/get/all/users")
requests.post("http://127.0.0.1:9089/create/post", data={"username": "Kiana", "size": "S", "category": "Dress"}, headers=header)
requests.post("http://127.0.0.1:9089/create/review", data={"username": "Kiana", "review": "Mycket bra", "post_id": 1}, headers=header)
requests.post("http://127.0.0.1:9089/create/review", data={"username": "Kiana", "review": "Inte så bra", "post_id": 1}, headers=header)
requests.post("http://127.0.0.1:9089/book", data={"username": "Kiana", "date": "2020-05-25", "post_id": 1}, headers=header)