#Acest script incarca imaginea Docker salvata/creata in rma build-tar.sh
#Ruleaza containerul
ROOT_PATH="$(cd "$(dirname "$0")" && pwd)"
BACKEND_PATH="$(dirname "$ROOT_PATH")"
TAR_PATH="$BACKEND_PATH/production/neforii-backend.tar"
IMAGE_NAME="neforii-backend:latest"

docker load -i "$TAR_PATH"

#-it e o combinatie de 2 flaguri, --interactive care mentine deschis STDIn si --tty pt terminal
docker run -it "$IMAGE_NAME"