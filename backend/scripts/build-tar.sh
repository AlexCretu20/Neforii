ROOT_PATH=$(dirname "$(dirname "$0")")
#$0 reprezinta calea scriptului curent, dirname da directorul
BACKEND_PATH="$ROOT_PATH"
PRODUCTION_PATH="$ROOT_PATH/production"

IMAGE_NAME="neforii-backend"
TAR_NAME="$IMAGE_NAME.tar"

cd "$BACKEND_PATH"

mvn clean package

docker build -t "$IMAGE_NAME" .

mkdir -p "$PRODUCTION_PATH"
docker save "$IMAGE_NAME" -o "$PRODUCTION_PATH/$TAR_NAME"

docker build -t

#!!TO DO -> shell script RUN care ruleaza build-tar.sh si urmatoarele 2 comenzi:
#imaginea se incarca cu "docker load -i backend/production/neforii-backend.tar"
#run-> "docker run -it neforii-backend:latest"