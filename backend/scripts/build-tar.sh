#Acest script construieste imaginea Docker din Dockerfile(cu maven si java), creeaza .jar
#salveaza imaginea Docker ca fisier .tar in backend/production

#este doar build, pentru run exista load-run.sh sau build-and-run.sh care le face pe ambele

ROOT_PATH="$(cd "$(dirname "$0")" && pwd)"
#$0 reprezinta calea scriptului curent gen scripts/build-tar.sh
#dirname scoate doar folderul adica scripts

#/backend/
BACKEND_PATH="$(dirname "$ROOT_PATH")"
PRODUCTION_PATH="$BACKEND_PATH/production"

IMAGE_NAME="neforii-backend"
TAR_NAME="$IMAGE_NAME.tar"

#imaginea Docker care are si maven
cd "$BACKEND_PATH"
docker build -t "$IMAGE_NAME" -f "$BACKEND_PATH/Dockerfile" .

#salvam imaginea
mkdir -p "$PRODUCTION_PATH"
docker save "$IMAGE_NAME" -o "$PRODUCTION_PATH/$TAR_NAME"