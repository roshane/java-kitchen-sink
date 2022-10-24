function echo(args){
    const input = JSON.parse(args)
    const result = input.data.map(n=>n*2);
    return JSON.stringify(result);
};