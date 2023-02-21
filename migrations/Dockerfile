FROM python:3-alpine

WORKDIR /usr/src/app

RUN apk add --no-cache gcc musl-dev python3-dev libffi-dev openssl-dev

COPY requirements.txt ./

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

ENTRYPOINT [ "python", "./cli.py" ]
